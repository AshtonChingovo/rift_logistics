package com.logistics.riftvalley.App;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.logistics.riftvalley.R;
import com.logistics.riftvalley.data.model.DB.AppDatabase;
import com.logistics.riftvalley.data.model.Dao.DispatchPicturesDao;
import com.logistics.riftvalley.data.model.Entity.DispatchPictures;
import com.logistics.riftvalley.Repository.ViewModels.PicturesViewModel;
import com.logistics.riftvalley.Utilities.SharedPreferences.SharedPreferencesClass;

import java.util.ArrayList;
import java.util.List;

public class PicturesNotUploadedList extends AppCompatActivity {

    private PicturesViewModel picturesViewModel;
    RecyclerView recyclerView;
    List<DispatchPictures> dispatchPictures = new ArrayList<>();
    FloatingActionButton floatingActionButton;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_dispatch_pictures);

        recyclerView = findViewById(R.id.recyclerView);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        floatingActionButton.setVisibility(View.INVISIBLE);

        //new GetPictures().execute();

        /*
         *   Observers
         * */
        final Observer<List<DispatchPictures>> picturesObserver = new Observer<List<DispatchPictures>>() {
            @Override
            public void onChanged(@Nullable List<DispatchPictures> dispatchPictures) {
                new GetPictures().execute();
            }
        } ;

        picturesViewModel = ViewModelProviders.of(this).get(PicturesViewModel.class);
        picturesViewModel.getDispatchPicturesLiveData(SharedPreferencesClass.getSalesOrderCustomerName()).observe(this, picturesObserver);


    }

    /**
     * Recycler View code
     **/
    public class RecyclerViewAdapter extends RecyclerView.Adapter<PicturesRecyclerViewHolder> {


        @NonNull
        @Override
        public PicturesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new PicturesRecyclerViewHolder(LayoutInflater.from(PicturesNotUploadedList.this).inflate(R.layout.dispatch_pictures_list_items, null, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PicturesRecyclerViewHolder picturesRecyclerViewHolder, int position) {
            picturesRecyclerViewHolder.bind(position);
        }

        @Override
        public int getItemCount() {
            return dispatchPictures.size();
        }
    }

    public class PicturesRecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView customerName;
        TextView date;
        TextView status;


        public PicturesRecyclerViewHolder(@NonNull View view) {
            super(view);

            customerName = view.findViewById(R.id.customerName);
            date = view.findViewById(R.id.date);
            status = view.findViewById(R.id.status);

        }

        public void bind(int position) {

            customerName.setText(dispatchPictures.get(position).getSalesOrderCustomer());
            date.setText(dispatchPictures.get(position).getDate());
            status.setText("Not Uploaded");

        }

    }

    public class GetPictures extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            DispatchPicturesDao dispatchPicturesDao = AppDatabase.getDatabase(getApplicationContext()).dispatchPicturesDao();

            dispatchPictures = dispatchPicturesDao.getAllDispatchPictures();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            recyclerView.setAdapter(new RecyclerViewAdapter());
            recyclerView.setLayoutManager(new LinearLayoutManager(PicturesNotUploadedList.this));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
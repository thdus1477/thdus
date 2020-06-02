package com.example.geofence_prac;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Extra_Todolist extends AppCompatActivity {

    Button btnAdd;
    RecyclerView recyclerView;                  //리사이클러뷰
    RecyclerAdapter recyclerAdapter;

    DatabaseHelper databaseHelper;
    ArrayList<GeofencingMemo> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra__todolist);

        recyclerView=findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(Extra_Todolist.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        databaseHelper = new DatabaseHelper(this);
        arrayList = databaseHelper.getAllText();

        recyclerAdapter=new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(recyclerAdapter);
        btnAdd=findViewById(R.id.writeButton);


        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //새로운 메모작성
                Intent intent=new Intent(Extra_Todolist.this, MapsActivity.class);
                startActivityForResult(intent,1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==requestCode){
            if(resultCode==1){
                String place =data.getStringExtra("place");
                String contents = data.getStringExtra("contents");

                GeofencingMemo memo = new GeofencingMemo(0, place, contents);
                recyclerAdapter.addItem(memo);
                recyclerAdapter.notifyDataSetChanged();

                databaseHelper.addMemo(memo);

            }
        }
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>{
        private List<GeofencingMemo> listdata;

        public RecyclerAdapter(List<GeofencingMemo> listdata){
            this.listdata=listdata;
        }

        @Override
        public int getItemCount() {
            return listdata.size();
        }


        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.geo_memo_item,viewGroup,false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            GeofencingMemo memo=listdata.get(i);

            itemViewHolder.maintext.setTag(memo.getId());

            itemViewHolder.maintext.setText(memo.getPlaceText());
            itemViewHolder.subtext.setText(memo.getContentText());
        }

        void addItem(GeofencingMemo memo){
            listdata.add(memo);
        }

        void removeItem(int position){
            listdata.remove(position);
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            private TextView maintext;
            private TextView subtext;

            public ItemViewHolder(@NonNull View itemView){
                super(itemView);

                maintext=itemView.findViewById(R.id.PlaceTextView);
                subtext=itemView.findViewById(R.id.ContentTextView);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {             //메모 리스트에서 원하는 아이템 길게 누르면 데이터 삭제
                        int position = getAdapterPosition();
                        int id = (int)maintext.getTag();

                        if(position != RecyclerView.NO_POSITION){
                            databaseHelper.deleteMemo(id);
                            removeItem(position);
                            notifyDataSetChanged();
                        }

                        return false;
                    }
                });
            }


        }
    }

}

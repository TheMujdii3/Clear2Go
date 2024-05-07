    package com.example.clear2go;

    import android.content.ClipData;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;

    import java.util.ArrayList;
    import java.util.HashMap;


    public class controlRecycleAdapter extends RecyclerView.Adapter<controlRecycleAdapter.MyViewHolder> {
        private ArrayList<Request> requests;

        private HashMap<MyViewHolder, Integer> positionMap = new HashMap<>();
        public controlRecycleAdapter(ArrayList<Request> requests)
        {
            this.requests=requests;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView request,requester;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                request=itemView.findViewById(R.id.request);
                requester=itemView.findViewById(R.id.requester);
            }
        }
        @NonNull
        @Override
        public controlRecycleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.requests_layout,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull controlRecycleAdapter.MyViewHolder holder, int position) {
            String plane = requests.get(position).getRequester();
            holder.requester.setText(plane);
            String rq=requests.get(position).getRequest();
            holder.request.setText(rq);
            positionMap.put(holder, requests.size());
            Button denyButt=holder.itemView.findViewById(R.id.denyRq);
            Button allowButt=holder.itemView.findViewById(R.id.allowRq);
            denyButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference().child("Requests/"+plane+"/"+rq).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/"+plane+"/"+rq).setValue(false);
                    MyViewHolder holder = (MyViewHolder) v.getTag();  // Assuming you set a tag on the view holder
                    if (holder != null && positionMap.containsKey(holder)) {
                        int position = positionMap.get(holder);
                        removeItem(position);
                        positionMap.remove(holder);  // Remove from map after removal
                    }
                }
            });
            allowButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference().child("Requests/"+plane+"/"+rq).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane/"+plane+"/"+rq).setValue(true);
                    MyViewHolder holder = (MyViewHolder) v.getTag();  // Assuming you set a tag on the view holder
                    if (holder != null && positionMap.containsKey(holder)) {
                        int position = positionMap.get(holder);
                        removeItem(position);
                        positionMap.remove(holder);  // Remove from map after removal
                    }
                }
            });
        }




        public void updateDataSet(ArrayList<Request> requests) {
            this.requests = (ArrayList<Request>) requests;
            notifyDataSetChanged();
        }

        public void removeItem(int position) {
            if (position >= 0 && position < requests.size()) {

                requests.remove(position);


                notifyItemRemoved(position);
            }
        }
        @Override
        public int getItemCount() {
            return requests.size();
        }
    }


    /*
    public class controlRecycleAdapter extends RecyclerView.Adapter<controlRecycleAdapter.MyViewHolder> {
        private HashMap<String, Request> requests;  // Use HashMap to store requests with key (requestId)

        public controlRecycleAdapter(HashMap<String, Request> requests) {
            this.requests = requests;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView requestName, requestValue;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                requestName = itemView.findViewById(R.id.requester); // Assuming separate TextViews for name and value
                requestValue = itemView.findViewById(R.id.request);
            }
        }

        @NonNull
        @Override
        public controlRecycleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.requests_layout, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull controlRecycleAdapter.MyViewHolder holder, int position) {
            // Extract request data based on position (assuming order is maintained)
            for (Map.Entry<String, Request> entry : requests.entrySet()) {
                holder.requestName.setText(entry.getKey()); // Set request name (key)
                holder.requestValue.setText(entry.getValue().getRequest()); // Set request value
                break; // Exit after setting data for the current position
            }
        }

        public void updateDataSet(HashMap<String, Request> requests) {
            this.requests = requests;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }

     */




package com.example.assistantme;


import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.logging.Handler;


public class chat_adapter extends RecyclerView.Adapter<chat_adapter.view_holder> {
    @NonNull
    @Override
    public view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bot,parent,false);
        return new view_holder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull view_holder holder, int position) {
     Animation slide_left= AnimationUtils.loadAnimation(holder.itemView.getContext(),android.R.anim.slide_in_left);
     holder.itemView.startAnimation(slide_left);
     RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.cardView.getLayoutParams();
     if(position %2==0){
         params.addRule(RelativeLayout.ALIGN_PARENT_END,0);
        params.addRule(RelativeLayout.ALIGN_PARENT_START,1);
         holder.chat_text.setText(MainActivity.chat_messages.get(position));
     }
     else{
         params.addRule(RelativeLayout.ALIGN_PARENT_START,0);
         params.addRule(RelativeLayout.ALIGN_PARENT_END,1);
         holder.chat_text.setText(MainActivity.chat_messages.get(position));
     }
    }
    @Override
    public int getItemCount() {
        return MainActivity.chat_messages.size();
    }

    public class view_holder extends RecyclerView.ViewHolder {

        TextView chat_text;
        CardView cardView;
        public view_holder(@NonNull View itemView) {
            super(itemView);
            chat_text=itemView.findViewById(R.id.bot_text);
            cardView=itemView.findViewById(R.id.card_view_chat);
        }
    }

}

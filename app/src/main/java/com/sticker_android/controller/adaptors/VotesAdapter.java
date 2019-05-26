package com.sticker_android.controller.adaptors;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sticker_android.R;
import com.sticker_android.controller.activities.common.signin.SigninActivity;
import com.sticker_android.model.Votes;
import com.sticker_android.network.ApiCall;
import com.sticker_android.network.ApiConstant;
import com.sticker_android.network.ApiResponse;
import com.sticker_android.network.RestClient;
import com.sticker_android.utils.AppLogger;
import com.sticker_android.utils.Utils;
import com.sticker_android.utils.sharedpref.AppPref;
import com.sticker_android.view.CircularImageView;

import java.util.List;

import retrofit2.Call;

public class VotesAdapter extends RecyclerView.Adapter<VotesAdapter.ViewHolder> {
    private Context context;
    private List<Votes> my_data;
    private AppPref appPref;


    public VotesAdapter(Context context, List<Votes> my_data, AppPref appPref) {
        this.context = context;
        this.my_data = my_data;
        this.appPref = appPref;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vote_item, parent, false);
        return new VotesAdapter.ViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Votes model = my_data.get(position);


        if (model.getDidUserVoted()) {
            hide_btns(holder);
            if (model.getUserVote().equals("1")) {
                holder.first_check.setVisibility(View.VISIBLE);
            } else if (model.getUserVote().equals("2")) {
                holder.second_check.setVisibility(View.VISIBLE);
            }
        } else {
            show_btns(holder);
            holder.first_check.setVisibility(View.GONE);
            holder.second_check.setVisibility(View.GONE);
        }

        holder.vote_user_name.setText(model.getFirstName() + " " + model.getLastName());
        holder.vote_time.setText(Utils.get_Time(model.getCreatedAt()));
        holder.vote_description.setText(model.getVoteDesc());
        holder.vote_category_name.setText(model.getCategoryName());
        holder.first_desc.setText(model.getFirstChoiceDesc());
        holder.second_desc.setText(model.getSecondChoiceDesc());
        holder.first_percentage.setText(Utils.getPercentage(model.getNoFirstVotes(), Integer.parseInt(model.getNoVotes())) + "%");
        holder.second_percentage.setText(Utils.getPercentage(model.getNoSecondVotes(), Integer.parseInt(model.getNoVotes())) + "%");
        holder.first_count.setText(model.getNoFirstVotes() + context.getString(R.string.user));
        holder.second_count.setText(model.getNoSecondVotes() + context.getString(R.string.user));

        if (model.getFirstChoiceImg() != null && !model.getFirstChoiceImg().isEmpty()) {
            holder.first_progress_bar.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(ApiConstant.IMAGE_URl + model.getFirstChoiceImg())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.first_progress_bar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.first_progress_bar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.first_vote_image);
        } else {
            holder.first_vote_image.setBackgroundColor(ContextCompat.getColor(context, R.color.image_background_color));
        }

        if (model.getSecondChoiceImg() != null && !model.getSecondChoiceImg().isEmpty()) {
            holder.second_progress_bar.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(ApiConstant.IMAGE_URl + model.getSecondChoiceImg())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            holder.second_progress_bar.setVisibility(View.GONE);

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.second_progress_bar.setVisibility(View.GONE);

                            return false;
                        }
                    })
                    .into(holder.second_vote_image);
        } else {
            holder.second_vote_image.setBackgroundColor(ContextCompat.getColor(context, R.color.image_background_color));
        }

        holder.first_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (appPref.getLoginFlag(false)) {

                    vote_api(appPref.getUserInfo().getId() , model.getId() , "1");
                    hide_btns(holder);
                    holder.first_check.setVisibility(View.VISIBLE);

                    holder.first_percentage.setText(Utils.getPercentage(model.getNoFirstVotes()+1, Integer.parseInt(model.getNoVotes())+1 ) + "%");
                    holder.second_percentage.setText(Utils.getPercentage(model.getNoSecondVotes(), Integer.parseInt(model.getNoVotes())+1) + "%");
                    holder.first_count.setText(model.getNoFirstVotes()+1 + context.getString(R.string.user));
                    holder.second_count.setText(model.getNoSecondVotes() + context.getString(R.string.user));



                } else {
                    Utils.Login_required((Activity) context);
                }


            }
        });

        holder.second_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (appPref.getLoginFlag(false)) {

                    vote_api(appPref.getUserInfo().getId() , model.getId() , "2");
                    hide_btns(holder);
                    holder.second_check.setVisibility(View.VISIBLE);

                    holder.first_percentage.setText(Utils.getPercentage(model.getNoFirstVotes(), Integer.parseInt(model.getNoVotes())+1) + "%");
                    holder.second_percentage.setText(Utils.getPercentage(model.getNoSecondVotes()+1, Integer.parseInt(model.getNoVotes())+1) + "%");
                    holder.first_count.setText(model.getNoFirstVotes() + context.getString(R.string.user));
                    holder.second_count.setText(model.getNoSecondVotes()+1 + context.getString(R.string.user));

                } else {
                    Utils.Login_required((Activity) context);
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return my_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView vote_user_name, vote_time, vote_description, vote_category_name, first_desc, second_desc, first_percentage, second_percentage, first_count, second_count;
        CircularImageView vote_user_image;
        ImageView first_vote_image, second_vote_image, first_check, second_check;
        Button first_btn, second_btn;
        ProgressBar first_progress_bar, second_progress_bar;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            vote_user_name = (TextView) view.findViewById(R.id.user_name);
            vote_time = (TextView) view.findViewById(R.id.vote_time);
            vote_description = (TextView) view.findViewById(R.id.vote_desc);
            vote_category_name = (TextView) view.findViewById(R.id.category_name);
            first_desc = (TextView) view.findViewById(R.id.first_choice_desc);
            second_desc = (TextView) view.findViewById(R.id.second_choice_desc);
            first_percentage = (TextView) view.findViewById(R.id.first_choice_percentage);
            second_percentage = (TextView) view.findViewById(R.id.second_choice_percentage);
            first_count = (TextView) view.findViewById(R.id.first_choice_count);
            second_count = (TextView) view.findViewById(R.id.second_choice_count);
            vote_user_image = (CircularImageView) view.findViewById(R.id.user_profile_image);
            first_vote_image = (ImageView) view.findViewById(R.id.first_choice_img);
            second_vote_image = (ImageView) view.findViewById(R.id.second_choice_img);
            first_check = (ImageView) view.findViewById(R.id.first_check_circle);
            second_check = (ImageView) view.findViewById(R.id.second_check_circle);
            first_btn = (Button) view.findViewById(R.id.first_btn_vote);
            second_btn = (Button) view.findViewById(R.id.second_btn_vote);
            first_progress_bar = (ProgressBar) view.findViewById(R.id.first_pgrImage);
            second_progress_bar = (ProgressBar) view.findViewById(R.id.second_pgrImage);

        }
    }

    void hide_btns(ViewHolder holder) {
        holder.first_btn.setVisibility(View.GONE);
        holder.second_btn.setVisibility(View.GONE);
    }

    void show_btns(ViewHolder holder) {
        holder.first_btn.setVisibility(View.VISIBLE);
        holder.second_btn.setVisibility(View.VISIBLE);
    }

    void vote_api(String user_id , String vote_id , String vote) {

        Call<ApiResponse> apiResponseCall= RestClient.getService().userVote(user_id , vote_id , vote);

        apiResponseCall.enqueue(new ApiCall((Activity) context) {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
            }

            @Override
            public void onFail(Call<ApiResponse> call, Throwable t) {

            }
        });


    }

}

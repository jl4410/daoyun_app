package cn.edu.fzu.daoyun_app.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.edu.fzu.daoyun_app.Member;
import cn.edu.fzu.daoyun_app.R;

public class MemberAdapter extends ArrayAdapter<Member> {

    private int resourceId;

    public MemberAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<Member> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Member member = getItem(position);
        final View view;
        final ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ranking = view.findViewById(R.id.No_Tv);
            viewHolder.memberIcon = view.findViewById(R.id.member_icon);
            viewHolder.memberName = view.findViewById(R.id.member_name_Tv);
            viewHolder.stuId = view.findViewById(R.id.member_number_Tv);
            viewHolder.experienceScore = view.findViewById(R.id.experience_score_Tv);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.ranking.setText(member.getRanking());
        if(member.getImageId() == -1){
            viewHolder.memberIcon.setImageBitmap(BitmapFactory.decodeFile(member.getIconFilePath()));
        }else if(member.getIconFilePath().equals("")){
            viewHolder.memberIcon.setImageResource(member.getImageId());
        }
        viewHolder.memberName.setText(member.getMemberName());
        viewHolder.stuId.setText(member.getStu_id());
        viewHolder.experienceScore.setText(member.getExperience_score()+"经验值");
        return view;
    }

    class ViewHolder{
        TextView ranking;
        ImageView memberIcon;
        TextView memberName;
        TextView stuId;
        TextView experienceScore;
    }

}


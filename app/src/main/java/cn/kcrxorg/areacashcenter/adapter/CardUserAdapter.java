package cn.kcrxorg.areacashcenter.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import cn.kcrxorg.areacashcenter.R;
import cn.kcrxorg.areacashcenter.data.model.msg.UserQueryMsg;
import cn.kcrxorg.areacashcenter.mbutil.BitmapUtils;

public class CardUserAdapter extends BaseAdapter {

     UserQueryMsg[] userQueryMsgs;
     Context context;

     public CardUserAdapter(Context context,UserQueryMsg[] userQueryMsgs)
     {
         this.context=context;
         this.userQueryMsgs=userQueryMsgs;
     }


    @Override
    public int getCount() {
        return userQueryMsgs.length;
    }

    @Override
    public Object getItem(int i) {
        return userQueryMsgs[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

      try {
          PackageViewHolder holder;
          if(view==null)
          {
              holder=new PackageViewHolder();
              view= LayoutInflater.from(context).inflate(R.layout.item_carduser,null);
              holder.tv_cardusername=view.findViewById(R.id.tv_cardusername);
              holder.tv_bankname=view.findViewById(R.id.tv_bankname);
              holder.iv_iccardimage=view.findViewById(R.id.iv_iccardimage);
              view.setTag(holder);
          }else
          {
              holder=(PackageViewHolder)view.getTag();
          }
          holder.tv_cardusername.setText(userQueryMsgs[i].getUserName());
          holder.tv_bankname.setText(userQueryMsgs[i].getBankName());
          String base64=userQueryMsgs[i].getIdImage();
          byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
          Bitmap bitmap= BitmapUtils.decodeSampledBitmapFromBitmap(decodedString,300,400);
          holder.iv_iccardimage.setImageBitmap(bitmap);

          view.setAnimation(AnimationUtils.makeInAnimation(context, false));
      }catch (Exception e)
      {

      }



        return view;
    }

    private class PackageViewHolder {
        TextView tv_cardusername;
        TextView tv_bankname;
        ImageView iv_iccardimage;

    }
}

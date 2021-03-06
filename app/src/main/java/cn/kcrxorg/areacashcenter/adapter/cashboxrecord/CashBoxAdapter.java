package cn.kcrxorg.areacashcenter.adapter.cashboxrecord;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.kcrxorg.areacashcenter.R;
import cn.kcrxorg.areacashcenter.adapter.BusinessAdapter;
import cn.kcrxorg.areacashcenter.data.model.CashBox;

public class CashBoxAdapter extends BaseAdapter {

    List<CashBox> cashBoxList;
    Context context;

    public CashBoxAdapter(Context context,List<CashBox> cashBoxList)
    {
        this.context=context;
        this.cashBoxList=cashBoxList;
    }
    @Override
    public int getCount() {
        return cashBoxList.size();
    }

    @Override
    public Object getItem(int i) {
        return cashBoxList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        try
        {
            PackageViewHolder holder;
            if (view == null) {
                holder = new PackageViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.item_cashbox, null);
                holder.tv_cashboxcode = view.findViewById(R.id.tv_cashboxlist_cashboxcode);
                holder.tv_cashboxid = view.findViewById(R.id.tv_cashboxid);
                view.setTag(holder);
            } else {
                holder = (PackageViewHolder) view.getTag();
            }
            if (cashBoxList.get(i).getColor().equals("green")) {
                //     Log.e("kcrxtest","设置第"+i+"条为绿色箱号:"+cashBoxList.get(i).getCashBoxCode());
                view.setBackgroundResource(R.color.xui_btn_green_normal_color);
            } else {
                view.setBackgroundResource(R.color.white);
            }
            //  Log.e("kcrx","cardnum="+cashBoxList.get(i).getCashBoxCode());
            holder.tv_cashboxcode.setText(cashBoxList.get(i).getCashBoxCode() + "");
            holder.tv_cashboxid.setText("序号" + (i + 1));
//            //滚到最下
//            ListView lv=(ListView) view.getParent();
//            lv.setSelection(lv.getBottom());
            //  view.setAnimation(AnimationUtils.makeInAnimation(context, false));
        }catch (Exception e)
        {
            Log.e("kcrx","cashboxadapter exception="+e.getMessage());
        }

        return view;
    }

    private class PackageViewHolder {
        TextView tv_cashboxid;
       TextView tv_cashboxcode;
    }
}

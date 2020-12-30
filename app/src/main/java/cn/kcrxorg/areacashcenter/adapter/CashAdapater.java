package cn.kcrxorg.areacashcenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.kcrxorg.areacashcenter.R;
import cn.kcrxorg.areacashcenter.data.Cash;

public class CashAdapater  extends BaseAdapter {

    List<Cash> cashList;
    Context context;

    public CashAdapater(Context context,List<Cash> cashList)
    {
        this.cashList=cashList;
        this.context=context;
    }

    @Override
    public int getCount() {
        return cashList.size();
    }

    @Override
    public Object getItem(int i) {
        return cashList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        PackageViewHolder holder;
        if(view==null)
        {
            holder = new PackageViewHolder();
            view= LayoutInflater.from(context).inflate(R.layout.item_cash,null);
            holder.tv_cashvoucher=view.findViewById(R.id.tv_cashvoucher);
            holder.tv_cashtype=view.findViewById(R.id.tv_cashtype);
            holder.tv_physicaltype=view.findViewById(R.id.tv_physicaltype);
            holder.tv_cashMoney=view.findViewById(R.id.tv_cashMoney);
            view.setTag(holder);
        }else
        {
            holder = (PackageViewHolder) view.getTag();
        }

        if(cashList!=null&&cashList.size()>0)
        {
            holder.tv_cashvoucher.setText(cashList.get(i).getCashVoucherId());
            holder.tv_cashtype.setText(cashList.get(i).getCashTypeId());
            holder.tv_physicaltype.setText(cashList.get(i).getPhysicalTypeId());
            holder.tv_cashMoney.setText(cashList.get(i).getCashMoney());

            String cashname=cashList.get(i).getCashVoucherId();
            if(cashname.contains("100元券"))
            {
                holder.tv_cashvoucher.setBackgroundColor(view.getResources().getColor(R.color.cash100));
                holder.tv_cashvoucher.setTextColor(view.getResources().getColor(R.color.white));
            }else if(cashname.contains("50元券"))
            {
                holder.tv_cashvoucher.setBackgroundColor(view.getResources().getColor(R.color.cash50));
                holder.tv_cashvoucher.setTextColor(view.getResources().getColor(R.color.white));
            }else if(cashname.contains("20元券"))
            {
                holder.tv_cashvoucher.setBackgroundColor(view.getResources().getColor(R.color.cash20));
                holder.tv_cashvoucher.setTextColor(view.getResources().getColor(R.color.white));
            }else if(cashname.contains("10元券"))
            {
                holder.tv_cashvoucher.setBackgroundColor(view.getResources().getColor(R.color.cash10));
                holder.tv_cashvoucher.setTextColor(view.getResources().getColor(R.color.white));
            }else if(cashname.contains("5元券"))
            {
                holder.tv_cashvoucher.setBackgroundColor(view.getResources().getColor(R.color.cash5));
                holder.tv_cashvoucher.setTextColor(view.getResources().getColor(R.color.white));
            }else if(cashname.contains("1元券"))
            {
                holder.tv_cashvoucher.setBackgroundColor(view.getResources().getColor(R.color.cash1));
                holder.tv_cashvoucher.setTextColor(view.getResources().getColor(R.color.white));
            }

            view.setAnimation(AnimationUtils.makeInAnimation(context, false));
        }
        return view;
    }

    private class PackageViewHolder {
        TextView tv_cashvoucher;
        TextView tv_cashtype;
        TextView tv_physicaltype;
        TextView tv_cashMoney;
    }
}

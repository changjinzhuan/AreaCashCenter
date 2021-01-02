package cn.kcrxorg.areacashcenter.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import cn.kcrxorg.areacashcenter.R;
import cn.kcrxorg.areacashcenter.adapter.cashboxrecord.CashBoxAdapter;
import cn.kcrxorg.areacashcenter.data.cashBoxRecord.Business;

public class BusinessAdapter extends BaseAdapter {

    List<Business> businessList;
    Context context;

    public BusinessAdapter(Context context,List<Business> businessList)
    {
        this.context=context;
        this.businessList=businessList;
    }

    @Override
    public int getCount() {
        return businessList.size();
    }

    @Override
    public Object getItem(int i) {
        return businessList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        try{
            PackageViewHolder holder;
            if(view==null)
            {
                holder = new PackageViewHolder();
                view= LayoutInflater.from(context).inflate(R.layout.item_business,null);
                holder.tv_bankName=view.findViewById(R.id.tv_bankName);
                holder.tv_servicetypename=view.findViewById(R.id.tv_servicetypename);
                holder.tv_linename=view.findViewById(R.id.tv_linename);
                holder.tv_updatetime=view.findViewById(R.id.tv_updatetime);
                holder.tv_distributetime=view.findViewById(R.id.tv_distributetime);
                holder.tv_totalmoeny=view.findViewById(R.id.tv_totalmoeny);
                holder.lv_cashlist=view.findViewById(R.id.lv_cashlist);
                holder.lv_zkList=view.findViewById(R.id.lv_zkList);
                holder.lv_cashboxes=view.findViewById(R.id.lv_cashboxes);
                view.setTag(holder);
            }else
            {
                holder = (PackageViewHolder) view.getTag();
            }
            holder.tv_bankName.setText(businessList.get(i).getBankName());
            holder.tv_servicetypename.setText(businessList.get(i).getServiceTypeName());
            holder.tv_linename.setText(businessList.get(i).getLineName());
            holder.tv_updatetime.setText(businessList.get(i).getUpDateTime());
            holder.tv_distributetime.setText(businessList.get(i).getDistributeTime());
            holder.tv_totalmoeny.setText(formatTosepara(new BigDecimal(businessList.get(i).getTotalMoney()))+"元");
          //  Log.e("kcrx","当前gridviewid="+i);
            if(businessList.get(i).getCashList()!=null)
            {
                cn.kcrxorg.areacashcenter.adapter.cashboxrecord.CashAdapater  cashAdapater=new cn.kcrxorg.areacashcenter.adapter.cashboxrecord.CashAdapater(context,businessList.get(i).getCashList());
                holder.lv_cashlist.setAdapter(cashAdapater);
                holder.lv_cashlist.setNumColumns(3);
                setListViewHeightBasedOnChildren(holder.lv_cashlist);
              //  ViewGroup.LayoutParams cashListmaxheight = getGridHeight(cashAdapater, holder.lv_cashlist);
                //holder.lv_cashlist.setLayoutParams(cashListmaxheight);
            }
          //  Log.e("Kcrx","CashBoxList="+businessList.get(i).getCashBoxList().size());
            if(businessList.get(i).getCashBoxList()!=null)
            {
                CashBoxAdapter cashBoxAdapter=new CashBoxAdapter(context,businessList.get(i).getCashBoxList());
                holder.lv_cashboxes.setAdapter(cashBoxAdapter);
                ViewGroup.LayoutParams cashboxlistmaxheight = getListHeight(cashBoxAdapter, holder.lv_cashboxes);
                holder.lv_cashboxes.setLayoutParams(cashboxlistmaxheight);
            }

            view.setAnimation(AnimationUtils.makeInAnimation(context, false));
        }catch (Exception e)
        {
            Log.e("kcrx",e.getMessage());
        }


        return view;
    }

    private class PackageViewHolder {
        TextView tv_bankName;
        TextView tv_servicetypename;
        TextView tv_linename;
        TextView tv_updatetime;
        TextView tv_distributetime;
        TextView tv_totalmoeny;
        GridView lv_cashlist;
        ListView lv_zkList;
        ListView lv_cashboxes;
    }
    public static String formatTosepara(BigDecimal data) {
        DecimalFormat df = new DecimalFormat("#,###.00");
        return df.format(data.floatValue());
    }
    /**

     * @return返回ListView的LayoutParams

     */
    public static ViewGroup.LayoutParams getGridHeight(Adapter sa, GridView lv){
        View itemView = sa.getView(0, null, lv);

        itemView.measure(0,0);

        int h= itemView.getMeasuredHeight();
        Log.e("kcrx","gv MeasuredHeight="+h);
        Log.e("kcrx","lv.getCount="+lv.getCount());
        Log.e("kcrx","lv.getNumColumns()="+lv.getNumColumns());
        int totalHeight = lv.getCount()* h /4+h;
        Log.e("kcrx","设置gridview高度为："+totalHeight);
        ViewGroup.LayoutParams params = lv.getLayoutParams();

        //总高度=总的item的高度+item之间的分割线高度

      //  params.height = totalHeight+ (lv.getDividerHeight() * (sa.getCount()- 1));
        params.height = totalHeight;
        return params;
    }

    public static ViewGroup.LayoutParams getListHeight(Adapter sa, ListView lv){
        View itemView = sa.getView(0, null, lv);

        itemView.measure(0,0);

        int h= itemView.getMeasuredHeight();

        int totalHeight = lv.getCount()* h ;

        ViewGroup.LayoutParams params = lv.getLayoutParams();

        //总高度=总的item的高度+item之间的分割线高度

          params.height = totalHeight+ (lv.getDividerHeight() * (sa.getCount()- 1));
      //  params.height = totalHeight;
        return params;
    }

    public static void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 4;
        // listView.getNumColumns() = -1?;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        /*for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }*/
        View listItem = listAdapter.getView(0, null, listView);
        listItem.measure(0, 0);
        // 获取item的高度和
        int totalHeight = listItem.getMeasuredHeight();
        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (listAdapter.getCount() % col != 0) {
            totalHeight = totalHeight * (listAdapter.getCount() / col + 1);
        } else {
            totalHeight = totalHeight * (listAdapter.getCount() / col);
        }

        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        listView.setLayoutParams(params);
    }
}

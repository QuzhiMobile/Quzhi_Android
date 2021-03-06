package com.czjy.chaozhi.ui.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.czjy.chaozhi.R;
import com.czjy.chaozhi.model.bean.DataLibraryBean;
import com.czjy.chaozhi.util.CompletedView;

import java.util.List;

public class DataLibraryAdapter extends BaseQuickAdapter<DataLibraryBean, BaseViewHolder> {

    public DataLibraryAdapter(int layoutResId, @Nullable List<DataLibraryBean> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, DataLibraryBean item) {
        helper.setText(R.id.item_datalibrary_name, item.getFile_name());
        ImageView iconImgView = helper.getView(R.id.item_datalibrary_img);
        CompletedView mTasksView = (CompletedView) helper.getView(R.id.tasks_view);
        switch (item.getProgress()) {
            case -1:
                iconImgView.setImageResource(R.mipmap.ic_down);
                iconImgView.setVisibility(View.VISIBLE);
                mTasksView.setVisibility(View.GONE);
                break;
            case 101:
                iconImgView.setImageResource(R.mipmap.ic_down_ok);
                iconImgView.setVisibility(View.VISIBLE);
                mTasksView.setVisibility(View.GONE);
                break;
            default:
                iconImgView.setVisibility(View.GONE);
                mTasksView.setVisibility(View.VISIBLE);
                mTasksView.setProgress(item.getProgress());
                break;
        }

    }
}

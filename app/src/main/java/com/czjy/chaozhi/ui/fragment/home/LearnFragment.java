package com.czjy.chaozhi.ui.fragment.home;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.czjy.chaozhi.R;
import com.czjy.chaozhi.base.BaseFragment;
import com.czjy.chaozhi.global.Const;
import com.czjy.chaozhi.model.bean.PurchProduct;
import com.czjy.chaozhi.model.event.UpdateFgEvent;
import com.czjy.chaozhi.presenter.main.LearnPresenter;
import com.czjy.chaozhi.presenter.main.contract.LearnContract;
import com.czjy.chaozhi.ui.activity.datalibrary.DataLibraryActivity;
import com.czjy.chaozhi.ui.activity.web.SimpleWebActivity;
import com.czjy.chaozhi.ui.activity.web.WebDetailActivity;
import com.czjy.chaozhi.ui.adapter.LearnAdapter;
import com.czjy.chaozhi.util.CommonUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by huyg on 2018/9/29.
 */
public class LearnFragment extends BaseFragment<LearnPresenter> implements LearnContract.View, BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.live_img)
    ImageView mLiveImg;
    @BindView(R.id.live_title)
    TextView mLiveTitle;
    @BindView(R.id.live_teacher)
    TextView mLiveTeacher;
    @BindView(R.id.live_time)
    TextView mLiveTime;
    @BindView(R.id.learn_radio)
    RadioGroup mRadioGroup;
    @BindView(R.id.book_layout)
    ConstraintLayout bookLayout;
    @BindView(R.id.no_book_layout)
    ConstraintLayout noBookLayout;
    @BindView(R.id.library_layout)
    LinearLayout mLibrary;
    @BindView(R.id.live_start)
    TextView mLiveStart;
    @BindView(R.id.learn_live_layout)
    LinearLayout mLiveLayout;
    @BindView(R.id.live_into)
    TextView mLiveInto;
    @BindView(R.id.learn_product_title)
    TextView mLearnTitle;
    @BindView(R.id.line2)
    View mLine2;
    @BindView(R.id.learn_none)
    TextView mNone;
    @BindView(R.id.learn_live_title)
    TextView mLearnLiveTitle;
//    @BindView(R.id.refresh)
//    SmartRefreshLayout mRefresh;

    @OnClick({R.id.library_layout, R.id.doc_layout, R.id.live_layout, R.id.video_layout, R.id.live_into})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.library_layout:
                WebDetailActivity.action(mContext, Const.ROUTER_LIBRARY + productId, "题库");
                break;
            case R.id.doc_layout:
//                WebDetailActivity.action(mContext, Const.ROUTER_DOC + productId, "资料库"); //H5
                DataLibraryActivity.action(mContext, productId); //原生
                break;
            case R.id.live_layout:
                WebDetailActivity.action(mContext, Const.ROUTER_LIVE + productId, "直播课程");
                break;
            case R.id.video_layout:
                WebDetailActivity.action(mContext, Const.ROUTER_VIDEO + productId, "录播课程");
                break;
            case R.id.live_into:
                if (mLiveBean != null) {
                    SimpleWebActivity.action(mContext, this.mLiveBean.getLive_url(), this.mLiveBean.getLive_name());
                }
                break;
        }
    }

    private LearnAdapter mAdapter;
    private List<PurchProduct.NewestInfoBean.LearnCourseListBean> learnCourses;
    private List<PurchProduct> mPurchProducts;
    private List<Fragment> mFragments = new ArrayList<>();
    private CourseAdapter mViewpagerAdapter;
    private int productId;
    private PurchProduct.NewestInfoBean.LiveListBean mLiveBean;

    public static LearnFragment newInstance() {
        LearnFragment learnFragment = new LearnFragment();
        return learnFragment;
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_learn;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void init() {
        initView();
//        initRefresh();
    }

//    private void initRefresh() {
//        mRefresh.setEnableLoadMore(false);
//        mRefresh.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshLayout) {
//                initData();
//            }
//        });
//    }

    private void initView() {
        initRecycler();
    }

    private void initViewPager() {
        mViewpagerAdapter = new CourseAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mViewpagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                PurchProduct purchProduct = mPurchProducts.get(i);
                if (purchProduct != null) {
                    productId = purchProduct.getProduct_id();
                    PurchProduct.NewestInfoBean newestInfoBean = purchProduct.getNewest_info();
                    updateLearnCourse(newestInfoBean.getLearn_course_list());
                    updateLiveInfo(newestInfoBean.getLive_list());
                    RadioButton radioButton = (RadioButton) mRadioGroup.getChildAt(i);
                    radioButton.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void updateLearnCourse(List<PurchProduct.NewestInfoBean.LearnCourseListBean> learnCourses) {
        if (learnCourses != null && learnCourses.size() > 0) {
            mLearnTitle.setVisibility(View.VISIBLE);
            mLine2.setVisibility(View.VISIBLE);
            mNone.setVisibility(View.GONE);
        } else {
            mLearnTitle.setVisibility(View.GONE);
            mLine2.setVisibility(View.GONE);
            mNone.setVisibility(View.VISIBLE);
        }
        mAdapter.setNewData(learnCourses);
    }

    private void updateLiveInfo(List<PurchProduct.NewestInfoBean.LiveListBean> liveInfos) {
        if (liveInfos != null && liveInfos.size() > 0) {
            mLiveLayout.setVisibility(View.VISIBLE);
            mLearnLiveTitle.setVisibility(View.VISIBLE);
            PurchProduct.NewestInfoBean.LiveListBean liveListBean = liveInfos.get(0);
            this.mLiveBean = liveListBean;
            if (liveListBean != null) {
                mLiveTitle.setText(liveListBean.getLive_name());
                mLiveTeacher.setText(String.format("主讲讲师：%s", liveListBean.getTeacher()));
                mLiveTime.setText(String.format("开始时间：%s", liveListBean.getLive_st()));
                if (liveListBean.getStatus() == 0) {
                    mLiveStart.setText("即将开始");
                    mLiveStart.setVisibility(View.VISIBLE);
                    mLiveInto.setVisibility(View.GONE);
                } else if (liveListBean.getStatus() == 1) {
                    mLiveStart.setVisibility(View.GONE);
                    mLiveInto.setVisibility(View.VISIBLE);
                } else if (liveListBean.getStatus() == -1) {
                    mLiveStart.setVisibility(View.GONE);
                    mLiveInto.setVisibility(View.VISIBLE);
                    mLiveInto.setText("查看回放");
                }
            }
        } else {
            mLiveLayout.setVisibility(View.GONE);
            mLearnLiveTitle.setVisibility(View.GONE);
        }
    }

    private void initRecycler() {
        mRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new LearnAdapter(R.layout.item_learn_plan, learnCourses);
        mAdapter.setOnItemClickListener(this);
        mRecycler.setAdapter(mAdapter);
    }

    private void initData() {
        mPresenter.getPurchProducts();
    }

    @Override
    public void showPurchProducts(List<PurchProduct> purchProducts) {

        if (purchProducts != null && purchProducts.size() > 0) {
            noBookLayout.setVisibility(View.GONE);
            bookLayout.setVisibility(View.VISIBLE);
            this.mPurchProducts = purchProducts;

            PurchProduct purchProduct0 = mPurchProducts.get(0);
            if (purchProduct0 != null) {
                productId = purchProduct0.getProduct_id();
            }
            mFragments.clear();
            mRadioGroup.removeAllViews();
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(CommonUtil.dp2px(getActivity(), 8), CommonUtil.dp2px(getActivity(), 8));
            for (PurchProduct purchProduct : purchProducts) {
                RadioButton radioButton = new RadioButton(getActivity());
                layoutParams.leftMargin = CommonUtil.dp2px(getActivity(), 4);
                radioButton.setBackground(getActivity().getResources().getDrawable(R.drawable.selector_learn_check));
                radioButton.setButtonDrawable(null);
                mRadioGroup.addView(radioButton, layoutParams);
                mFragments.add(CourseFragment.newInstance(purchProduct));
            }
            updateLearnCourse(purchProducts.get(0).getNewest_info().getLearn_course_list());
            updateLiveInfo(purchProducts.get(0).getNewest_info().getLive_list());
            RadioButton radioButton = (RadioButton) mRadioGroup.getChildAt(0);
            radioButton.setChecked(true);
            initViewPager();
        } else {
            noBookLayout.setVisibility(View.VISIBLE);
            bookLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void finishRefresh() {
//        mRefresh.finishRefresh();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        List<PurchProduct.NewestInfoBean.LearnCourseListBean> listBeans = adapter.getData();
        PurchProduct.NewestInfoBean.LearnCourseListBean learnCourseListBean = listBeans.get(position);
        if (learnCourseListBean != null) {
            SimpleWebActivity.action(mContext, learnCourseListBean.getView_url(), learnCourseListBean.getName());
        }
    }

    class CourseAdapter extends FragmentStatePagerAdapter {

        public CourseAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(UpdateFgEvent event) {
        int index = event.index;
        if (index == 1) {
            initData();
            EventBus.getDefault().removeStickyEvent(event);
        }
    }
}

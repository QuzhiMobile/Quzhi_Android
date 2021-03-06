package com.czjy.chaozhi.presenter.user;

import com.czjy.chaozhi.base.RxPresenter;
import com.czjy.chaozhi.model.http.ApiFactory;
import com.czjy.chaozhi.model.http.BaseResponse;
import com.czjy.chaozhi.presenter.user.contract.ResetContract;
import com.czjy.chaozhi.util.rx.RxException;
import com.czjy.chaozhi.util.rx.RxSchedulers;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by huyg on 2018/9/28.
 */
public class ResetPresenter extends RxPresenter<ResetContract.View> implements ResetContract.Presenter {

    private ApiFactory mApiFactory;


    @Inject
    public ResetPresenter(ApiFactory apiFactory) {
        this.mApiFactory = apiFactory;
    }


    @Override
    public void resetPwd(String phone, String code, String pwd) {
        Disposable disposable = mApiFactory.getUserApi().reset(phone, pwd, code)
                .compose(RxSchedulers.io_main())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .subscribe(new Consumer<BaseResponse>() {
                    @Override
                    public void accept(BaseResponse response) throws Exception {
                        mView.closeProgress();
                        if (response.isSuccess()) {
                            mView.toast("密码找回成功，请重新登录");
                            mView.resetSuccess();
                        }
                        else {
                            mView.toast(response.getMsg());
                        }
                    }
                },new RxException<>(e->{
                    mView.closeProgress();
                    mView.toast(e.getMessage());
                }));
        addDispose(disposable);
    }

    @Override
    public void getCode(String phone, String type) {
        Disposable disposable = mApiFactory.getUserApi()
                .getCode(phone, type)
                .compose(RxSchedulers.io_main())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .subscribe(new Consumer<BaseResponse>() {
                    @Override
                    public void accept(BaseResponse response) throws Exception {
                        mView.closeProgress();
                        if (response.isSuccess()) {
                            mView.toast("验证码发送成功");
                        } else {
                            mView.toast(response.getMsg());
                        }
                    }
                }, new RxException<>(e -> {
                    mView.closeProgress();
                    mView.toast(e.getMessage());
                }));
        addDispose(disposable);
    }
}

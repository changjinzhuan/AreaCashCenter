/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.kcrxorg.areacashcenter.httputil;

import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.XHttpSDK;
import com.xuexiang.xhttp2.callback.DownloadProgressCallBack;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xupdate.proxy.IUpdateHttpService;
import com.xuexiang.xutil.file.FileUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.Map;

import io.reactivex.disposables.Disposable;

/**
 * XHttp2实现的请求更新
 *
 * @author xuexiang
 * @since 2018/8/12 上午11:46
 */
public class XHttpUpdateHttpService implements IUpdateHttpService {

    private String mBaseUrl;

    public XHttpUpdateHttpService(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    @Override
    public void asyncGet( String url,  Map<String, Object> params,  final IUpdateHttpService.Callback callBack) {
        XHttp.get(url)
                .baseUrl(mBaseUrl)
                .params(params)
                .keepJson(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String response) throws Throwable {
                        callBack.onSuccess(response);
                    }

                    @Override
                    public void onError(ApiException e) {
                        callBack.onError(e);
                    }
                });
    }

    @Override
    public void asyncPost( String url,  Map<String, Object> params,  final IUpdateHttpService.Callback callBack) {
        //这里默认post的是Form格式，使用json格式的请修改为 params -> upJson
        XHttp.post(url)
                .baseUrl(mBaseUrl)
                .params(params)
                .keepJson(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(String response) throws Throwable {
                        callBack.onSuccess(response);
                    }

                    @Override
                    public void onError(ApiException e) {
                        callBack.onError(e);
                    }
                });
    }

    @Override
    public void download( String url,  String path,  String fileName,  final IUpdateHttpService.DownloadCallback callback) {
        Disposable disposable = XHttp.downLoad(url)
                .savePath(path)
                .saveName(fileName)
                .isUseBaseUrl(false)
                .baseUrl(mBaseUrl)
                .execute(new DownloadProgressCallBack<String>() {
                    @Override
                    public void onStart() {
                        callback.onStart();
                    }

                    @Override
                    public void onError(ApiException e) {
                        callback.onError(e);
                    }

                    @Override
                    public void update(long downLoadSize, long totalSize, boolean done) {
                        callback.onProgress(downLoadSize / (float) totalSize, totalSize);
                    }

                    @Override
                    public void onComplete(String path) {
                        callback.onSuccess(FileUtils.getFileByPath(path));
                    }
                });

        XHttpSDK.addRequest(url, disposable);
    }

    @Override
    public void cancelDownload( String url) {
        ToastUtils.toast("已取消更新！");
        XHttpSDK.cancelRequest(url);
    }
}

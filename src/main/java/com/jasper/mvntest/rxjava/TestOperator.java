package com.jasper.mvntest.rxjava;

import rx.Observable;
import rx.functions.Func1;

import java.util.Arrays;
import java.util.List;

/**
 * 操作符相关
 */
public class TestOperator {
    public static void main(String[] args) {
        query("Hello, world!")
                .subscribe(urls -> {
                    for (String url : urls) {
                        System.out.println(url);
                    }
                });

        query("Hello, world!")
                .subscribe(urls -> {
                    Observable.from(urls)
                            .subscribe(url -> System.out.println(url));
                });

        query("Hello, world!")
                .flatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<String> urls) {
                        return Observable.from(urls);
                    }
                })
                .subscribe(url -> System.out.println(url));

        query("Hello, world!")
                .flatMap(urls -> Observable.from(urls))
                .subscribe(url -> System.out.println(url));

        query("Hello, world!")
                .flatMap(urls -> Observable.from(urls))
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String url) {
                        return getTitle(url);
                    }
                })
                .subscribe(title -> System.out.println(title));

        query("Hello, world!")
                .flatMap(urls -> Observable.from(urls))
                .flatMap(url -> getTitle(url))
                .subscribe(title -> System.out.println(title));
    }

    public static Observable<List<String>> query(String text) {
        return Observable.create(subscribe -> {
            subscribe.onNext(Arrays.asList(text));
            subscribe.onCompleted();
        });
    }

    public static Observable<String> getTitle(String url) {
        return Observable.create(subscribe -> {
            subscribe.onNext("title:" + url);
            subscribe.onCompleted();
        });
    }
}

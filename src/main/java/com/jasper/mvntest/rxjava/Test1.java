package com.jasper.mvntest.rxjava;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * RX 全称Reactive Extensions
 */
public class Test1 {
    public static void main(String[] args) {
        // 创建observable
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("Hello RxJava 1");
                subscriber.onNext("Hello RxJava 2");
                subscriber.onNext("Hello RxJava 3");
                subscriber.onCompleted();
            }
        });

        // 创建subscriber
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext:" + s);
            }
        };

        // 订阅
        observable.subscribe(subscriber);

        System.out.println("============");

        // 第二种写法
        // 创建subscriber
        Observable.just("Hello RxJava")
                .subscribe(s -> System.out.println("call:" + s));

        System.out.println("============");

        Observable.just("#Basic Markdown to HTML").map(s -> {
            if (s != null && s.startsWith("#")) {
                return "<h1>" + s.substring(1, s.length()) + "</h1>";
            }
            return null;
        }).subscribe(s -> System.out.println("call:" + s));

        System.out.println("============");

        Subscription subscription = Observable.just("Unsubscribe me later").subscribe(s -> System.out.println("call:" + s));
        subscription.unsubscribe();
        System.out.println("isUnsubscribed = " + subscription.isUnsubscribed());

        System.out.println("============");

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                System.out.println("Observable on Thread -> " + Thread.currentThread().getName());
                subscriber.onNext("MultiThreading");
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    System.out.println("Subscriber on Thread -> " + Thread.currentThread().getName());
                });


        System.out.println("============");
        Observable.just("Hello, world!")
                .map(s -> s.hashCode())
                .map(i -> Integer.toString(i))
                .subscribe(s -> System.out.println(s));
    }
}

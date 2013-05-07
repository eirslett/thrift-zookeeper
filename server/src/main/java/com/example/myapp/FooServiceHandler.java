package com.example.myapp;

import com.example.myapp.thrift.Foo;
import com.example.myapp.thrift.FooService;
import com.twitter.util.Future;

public class FooServiceHandler implements FooService.FutureIface {
    private String bazz;
    public FooServiceHandler(String bazz) {
        this.bazz = bazz;
    }

    public Future<Foo> giveMeSomeFoo(int id) {
        // Domain objects are immutable. They come with constructors and builders.
        Foo foo = new Foo.Builder()
                .bar("test")
                .bazz("hey, this is a response from " + bazz)
                .squirrel(42)
                .build();

        // Finagle is async, so wrap the return value in a Future
        // if the implementation is synchronous
        return Future.value(foo);
    }
}

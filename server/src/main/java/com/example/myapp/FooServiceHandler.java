package com.example.myapp;

import com.example.myapp.thrift.*;
import com.twitter.ostrich.stats.Stats;
import com.twitter.scrooge.Option;
import com.twitter.util.Future;
import com.twitter.util.Throw;
import scala.Option$;

public class FooServiceHandler implements FooService.FutureIface {
    private String bazz;
    public FooServiceHandler(String bazz) {
        this.bazz = bazz;
    }

    public Future<Foo> giveMeSomeFoo(int id) {
        Stats.incr("number_of_foo_calls"); // Report stats to Ostrich

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

    @Override
    public Future<Foo> pleaseMore(String pass) {
        return Future.exception(new RuntimeException("fuckit, you got enough"));
    }
}

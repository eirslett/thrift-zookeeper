namespace  java com.example.myapp.thrift

struct Foo {
    1: string bar;
    2: string bazz;
    3: i32 squirrel;
    4: optional string mordi;
}

exception NoooException {
    1: string message;
    2: string apology;
}

service FooService {
    Foo giveMeSomeFoo(i32 id);
    Foo pleaseMore(string pass) throws (1: NoooException ex);
}
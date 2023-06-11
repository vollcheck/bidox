# bidox

cms when editing history hits hard

## client

on the development enviroment, application will run on port `4000`.

there's a bidox client as well, it will run on port `3000`.

## time travel

xtdb allows you to travel in time which is cool:

```console
GET http://localhost:4000/api/document
{"title": "marketing mix"}

# results in:
[
  [
    {
      "doc/title": "marketing mix",
      "doc/content": "something something marketing mix",
      "xt/id": 1000
    }
  ]
]
```

but if we go back in time to the good ol' days with given _optional_ `timestamp` argument:

```console
GET http://localhost:4000/api/document
{"title": "marketing mix", "timestamp": "1999-12-31"}

# results in:

[
  [
    {
      "doc/title": "marketing mix",
      "doc/content": "old time-travelled content for marketing mix",
      "xt/id": 1000
    }
  ]
]
```

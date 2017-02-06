# Akka API Template

Straightforwad scaffold of an Akka based web application API.

## Batteries Included

It's used in real world, everything you need to set up a real world application is considered here, take a look in the
next topics.

### Component based architecture

Instead of spreading functionalities over "framework"'s logic this template favors functionalities as first order
structure driver. This means that different layers of a same functionality are placed together!

```sh
tree src/main/scala/com/nykolaslima/akka-api-template/components/user
├── ActorMessages.scala
├── UserRepositoryActor.scala
├── UserRepository.scala
├── UserRoute.scala
├── User.scala
├── UserServiceActor.scala
└── UserValidator.scala
```

### Protocol buffer ready

This template can speak JSON and Protobuf protocols.

### Database

Through slick this project have a fully set up PostgreSQL configuration and structure counting with docker, migrations,
and more.

### Healthcheck

Comprehensive healthcheck that aims to provide fully discoverable info and quick check for load balancers.

## Contributing

There two main components, the template itself and the generator script. All ideas and contributions are welcome. Please,
concise and well described commits are a must!

1. Fork it
2. Cut a new branch
3. Commit and push your work
4. Open up a Pull Request

## Acknowledgements

Thanks @thiagoandrade6 for the first version of this architecture.

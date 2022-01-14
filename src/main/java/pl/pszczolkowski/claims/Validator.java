package pl.pszczolkowski.claims;

@FunctionalInterface
interface Validator<T> {

    T validate(T object);
}

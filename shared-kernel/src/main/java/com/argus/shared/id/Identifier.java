package com.argus.shared.id;

/** Contrat commun des identifiants typés : ils enveloppent une valeur. */
public interface Identifier<T> {

    T value();
}

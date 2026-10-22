/**
 * Erreurs de base et garde-fous d'invariants.
 *
 * <p>Ce package ne dépend d'aucun autre package du kernel : c'est la couche la plus basse.
 * Il fournit :
 * <ul>
 *   <li>{@link com.argus.shared.error.argusException} : racine de toutes les erreurs métier ;</li>
 *   <li>{@link com.argus.shared.error.ErrorCode} : code d'erreur stable, extensible par chaque module ;</li>
 *   <li>trois erreurs génériques : introuvable, entrée invalide, système externe en échec ;</li>
 *   <li>{@link com.argus.shared.error.Guard} : vérification d'invariants utilisée par tous les types du kernel.</li>
 * </ul>
 *
 * <p>Aucune notion HTTP ici : la traduction code d'erreur vers statut HTTP appartient à {@code bootstrap}.
 */
package com.argus.shared.error;

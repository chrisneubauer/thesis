package de.cneubauer.util.enumeration;

/**
 * Created by Christoph Neubauer on 10.01.2017.
 * State of Validation
 * OK: Validation successful, everything alright
 * MALFORMEDVALUE: Accounting record sums (debit and credit) do not sum up to 0
 * MISSINGACCOUNTS: At least one side of accounts is completely missing
 * MISSINGPOSITION: No position string has been selected
 * MISSINGVALUES: No values on at least one side has been selected
 * UNKNOWNISSUE: Any other invalid state
 */
public enum ValidationStatus {
    OK,
    MALFORMEDVALUE,
    MISSINGACCOUNTS,
    MISSINGPOSITION,
    MISSINGVALUES,
    UNKNOWNISSUE
}
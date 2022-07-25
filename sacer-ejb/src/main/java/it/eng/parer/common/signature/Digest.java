package it.eng.parer.common.signature;

/**
 * Constants about digest (hash of a file)
 *
 * @author Moretti_Lu
 */
public abstract class Digest {

    private Digest() {
    }

    /**
     * Type of encoding
     */
    public enum Encoding {
        base64
    }

    /**
     * Type of digest algorithm
     */
    public enum DigestAlgorithm {
        SHA1("SHA-1"), SHA256("SHA-256");

        private final String value;

        private DigestAlgorithm(final String alg) {
            this.value = alg;
        }

        public String getValue() {
            return this.value;
        }
    }
}
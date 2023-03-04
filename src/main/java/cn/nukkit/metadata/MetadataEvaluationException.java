package cn.nukkit.metadata;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * A MetadataEvaluationException is thrown any time a {@link
 * LazyMetadataValue} fails to evaluate its value due to an exception. The
 * originating exception will be included as this exception's cause.
 */
@SuppressWarnings("serial")
@PowerNukkitXOnly
@Since("1.19.63-r2")
public class MetadataEvaluationException extends RuntimeException {
    MetadataEvaluationException(Throwable cause) {
        super(cause);
    }
}
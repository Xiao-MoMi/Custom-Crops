package net.momirealms.customcrops.common.annotation;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@ApiStatus.Internal
@Target({ElementType.FIELD})
public @interface DoNotUse {
}

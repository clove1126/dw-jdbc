/*
 * dw-jdbc
 * Copyright 2017 data.world, Inc.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * This product includes software developed at data.world, Inc.(http://www.data.world/).
 */
package world.data.jdbc.internal.util;

import lombok.experimental.UtilityClass;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Function;

@UtilityClass
public class Optionals {

    public static <T> T or(T a, T b) {
        return a != null ? a : b;
    }

    public static <T, U> U mapIfPresent(T a, Function<T, U> ifPresentFn) {
        return a != null ? ifPresentFn.apply(a) : null;
    }

    public static <T> boolean nullOrEquals(@Nullable T a, T b) {
        return a == null || a.equals(b);
    }

    public static boolean nullOrMatches(@Nullable String pattern, String string) {
        return pattern == null || Like.matches(string, pattern);
    }

    public static <T> boolean nullOrContains(@Nullable T[] b, T a) {
        return b == null || Arrays.asList(b).contains(a);
    }
}

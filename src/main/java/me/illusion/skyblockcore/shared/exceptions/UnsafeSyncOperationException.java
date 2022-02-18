package me.illusion.skyblockcore.shared.exceptions;

import java.util.Arrays;
import java.util.List;

public class UnsafeSyncOperationException extends RuntimeException {

    @Override
    public String getMessage() {
        List<String> list = Arrays.asList("one", "two", "three");

        String result = String.join(", ", list);
        return "Called locking method in main thread!";
    }
}

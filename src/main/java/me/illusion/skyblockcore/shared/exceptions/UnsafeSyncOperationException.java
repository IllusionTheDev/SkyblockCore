package me.illusion.skyblockcore.shared.exceptions;

public class UnsafeSyncOperationException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Called locking method in main thread!";
    }
}

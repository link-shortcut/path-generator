package me.lnsc.pathgenerator.domain.exception;

public class PathExhaustionException extends RuntimeException {
    private static final String MESSAGE = "경로가 고갈되었습니다.";

    public PathExhaustionException() {
        super(MESSAGE);
    }
}

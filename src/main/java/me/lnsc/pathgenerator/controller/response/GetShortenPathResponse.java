package me.lnsc.pathgenerator.controller.response;

import java.util.List;

public record GetShortenPathResponse(
        List<String> paths
) {
}

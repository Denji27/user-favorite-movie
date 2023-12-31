package com.userfavoritemovie.usermovieservice.config;

import io.grpc.EquivalentAddressGroup;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceRegistry {
    private static final Map<String, List<EquivalentAddressGroup>> MAP = new HashMap<>();

    public static void register(String service, List<String> instances){
        List<EquivalentAddressGroup> addressGroupList = instances.stream()
                .map(i -> i.split(":"))
                .map(a -> new InetSocketAddress(a[0], Integer.parseInt(a[1])))
                .map(isa -> new EquivalentAddressGroup(isa))
                .collect(Collectors.toList());
        MAP.put(service, addressGroupList);
    }

    public static List<EquivalentAddressGroup> getInstances(String service){
        return MAP.get(service);
    }
}

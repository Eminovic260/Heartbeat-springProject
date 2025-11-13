package com.springbootproject.heartbeat.service;


import org.springframework.stereotype.Service;

@Service
public class RandomHeartbeat implements HeartbeatSensor{
    @Override
    public int get(){
        return 40 + (int) (Math.random() * (230 - 40 + 1));
    }
}

package com.example.coftea.Cashier.queue;

import com.example.coftea.Cashier.order.QueueEntry;

public class QueueViewModelProcessResult {
    private String error;
    private QueueEntry success;

    public QueueViewModelProcessResult(QueueEntry queueEntry){
        this.success = queueEntry;
    }

    public QueueViewModelProcessResult(String error){
        this.error = error;
    }
}

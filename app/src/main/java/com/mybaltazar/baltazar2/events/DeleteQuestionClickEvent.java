package com.mybaltazar.baltazar2.events;

import com.mybaltazar.baltazar2.models.Question;

public class DeleteQuestionClickEvent implements Event {
    public final Question item;

    public DeleteQuestionClickEvent(Question item) {
        this.item = item;
    }
}

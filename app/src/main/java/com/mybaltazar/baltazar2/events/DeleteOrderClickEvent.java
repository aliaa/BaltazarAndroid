package com.mybaltazar.baltazar2.events;

import com.mybaltazar.baltazar2.models.ShopOrder;

public class DeleteOrderClickEvent implements Event
{
    public final ShopOrder order;

    public DeleteOrderClickEvent(ShopOrder order) {
        this.order = order;
    }
}

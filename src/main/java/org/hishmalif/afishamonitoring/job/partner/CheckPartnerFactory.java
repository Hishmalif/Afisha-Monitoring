package org.hishmalif.afishamonitoring.job.partner;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.hishmalif.afishamonitoring.dto.MonitoringObject;
import org.hishmalif.afishamonitoring.job.partner.rambler.RamblerJob;
import org.hishmalif.afishamonitoring.job.partner.ticketscloud.TicketsCloudJob;

@Component
public class CheckPartnerFactory {
    private static ApplicationContext context;

    @Autowired
    private void setApplicationContext(ApplicationContext applicationContext) {
        CheckPartnerFactory.context = applicationContext;
    }

    public static CheckPartner getCheckPartner(MonitoringObject.Request request) {
        switch (request.getPartner()) {
            case RAMBLER:
                return context.getBean(RamblerJob.class);
            case TICKETSCLOUD:
                return context.getBean(TicketsCloudJob.class);
            default:
                throw new IllegalArgumentException("Unknown partner: " + request.getPartner());
        }
    }
}
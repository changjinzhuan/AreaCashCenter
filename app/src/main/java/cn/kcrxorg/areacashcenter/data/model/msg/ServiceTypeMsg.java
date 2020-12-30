package cn.kcrxorg.areacashcenter.data.model.msg;

import java.util.List;

import cn.kcrxorg.areacashcenter.data.model.ServiceType;

public class ServiceTypeMsg extends BaseMsg {
    public List<ServiceType> getServiceTypeList() {
        return serviceTypeList;
    }

    public void setServiceTypeList(List<ServiceType> serviceTypeList) {
        this.serviceTypeList = serviceTypeList;
    }

    List<ServiceType> serviceTypeList;
}

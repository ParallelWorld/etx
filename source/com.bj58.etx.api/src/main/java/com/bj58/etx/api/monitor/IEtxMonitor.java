package com.bj58.etx.api.monitor;

public interface IEtxMonitor {

    public void doTryError();

    public void doConfirmError();

    public void doCancelError();

    public void doServiceError();

    public void doAbsolutelyError();

    public void syncSuccess();

    public void syncFail();

    public void txTotal();
}

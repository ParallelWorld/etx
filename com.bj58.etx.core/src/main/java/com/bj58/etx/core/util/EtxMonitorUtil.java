package com.bj58.etx.core.util;

import com.bj58.etx.api.monitor.IEtxMonitor;
import com.bj58.etx.core.runtime.EtxRuntime;

public class EtxMonitorUtil {

	private static IEtxMonitor monitor = EtxRuntime.monitor;

	public static void doTryError() {
		if (monitor != null) {
			monitor.doTryError();
		}
	}

	public static void doConfirmError() {
		if (monitor != null) {
			monitor.doConfirmError();
		}
	}

	public static void doCancelError() {
		if (monitor != null) {
			monitor.doCancelError();
		}
	}

	public static void doServiceError() {
		if (monitor != null) {
			monitor.doServiceError();
		}
	}

	public static void doAbsolutelyError() {
		if (monitor != null) {
			monitor.doAbsolutelyError();
		}
	}
	
	public static void syncSuccess() {
		if (monitor != null) {
			monitor.syncSuccess();
		}
	}

	public static void syncFail() {
		if (monitor != null) {
			monitor.syncFail();
		}
	}

	public static void txTotal() {
		if (monitor != null) {
			monitor.txTotal();
		}
	}
}

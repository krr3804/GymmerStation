package com.gymmer.gymmerstation;

import com.gymmer.gymmerstation.programManagement.*;
import com.gymmer.gymmerstation.programOperation.*;

public class AppConfig {
    public static ProgramService programService() {
        return new ProgramServiceImpl();
    }

    public static ProgramOperationService programOperationService() {
        return new ProgramOperationServiceImpl();
    }

}

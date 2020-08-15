package com.mybank.api.report;

public class ReportFactory {

    public IReport getReportInstance(String type){
        if(type.equalsIgnoreCase("csv")){
            return new CsvReport();
        }else if(type.equalsIgnoreCase("xlsx") || type.equalsIgnoreCase("xlsx")) {
            return new XlsxReport();
        }else{
            throw new IllegalArgumentException("Invalid Report type " + type + " Allowed Report types csv/xlsx");
        }
    }

}

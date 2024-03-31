package com.hh.tools.sap.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.hh.tools.newitem.Utils;
import com.hh.tools.util.ConnectPoolUtils;
import com.hh.tools.util.QuoteDemo;
import com.hh.tools.util.StockDemo;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

public class CallSAPService {
    public static String QUOTEFuction = "ZRFC_MM_CES_0067";
    public static String STOCKFuction = "ZRFC_MM_CES_0068";
    public static String HHPNFuction = "ZRFC_GET_PROD_MASTER";
    public ArrayList<StockDemo> list = null;
    public StockDemo stockDemo = null;


    public CallSAPService() {

    }

    //查询报价
    public static List<QuoteDemo> getQuote(String[] HHPNArr, String[] plantArr, String org) {
        List<QuoteDemo> list = new ArrayList<>();
        try {
            JCoDestination destination = JCoDestinationManager
                    .getDestination(ConnectPoolUtils.ABAP_AS_POOLED);
            destination.ping();
            String strFunc = QUOTEFuction;
            JCoFunction funGetList = destination.getRepository().getFunction(
                    strFunc);
            JCoTable p_MAT_Table = funGetList.getTableParameterList().getTable(
                    "P_MAT");
            p_MAT_Table.deleteAllRows();
            for (int i = 0; i < HHPNArr.length; i++) {
                p_MAT_Table.appendRow();
                p_MAT_Table.setRow(i);
                p_MAT_Table.setValue("MATNR", HHPNArr[i]);
            }


            JCoTable p_PLANT_Table = funGetList.getTableParameterList().getTable(
                    "P_PLANT");
            p_PLANT_Table.deleteAllRows();

            for (int i = 0; i < plantArr.length; i++) {
                p_PLANT_Table.appendRow();
                p_PLANT_Table.setRow(i);
                p_PLANT_Table.setValue("WERKS", plantArr[i]);
            }


            JCoTable p_ORG_Table = funGetList.getTableParameterList().getTable(
                    "P_ORG");
            p_ORG_Table.deleteAllRows();
            p_ORG_Table.appendRow();
            p_ORG_Table.setRow(0);
            p_ORG_Table.setValue("EKORG", org);

            funGetList.execute(destination);
            JCoParameterList struct = funGetList.getExportParameterList();
            String MARK = (String) struct.getValue("MARK");
//			String MESSAGE = (String) struct.getValue("MESSAGE");
            if ("N".equals(MARK.toUpperCase())) {
            	Utils.infoMessage("SAP接口连接失败!");	
                return null;
            }

            JCoTable return_Table = funGetList.getTableParameterList().getTable(
                    "RETURN");
            if (return_Table == null || return_Table.getNumRows() == 0) {
            	Utils.infoMessage("未查询到有效数据,请检查!");	
                return null;
            } else {
                for (int i = 0; i < return_Table.getNumRows(); i++) {
                	String MATNR=(String)return_Table.getValue("MATNR"); //鸿海料号				
					BigDecimal KBETR=(BigDecimal)return_Table.getValue("KBETR");  //报价	
					String price = new DecimalFormat("0.00").format(KBETR);
					String KONWA=(String)return_Table.getValue("KONWA"); //币种
					String LIFNR=(String)return_Table.getValue("LIFNR"); //供应商
					String IDNLF=(String)return_Table.getValue("IDNLF"); //供应商料号
                    QuoteDemo quoteDemo = new QuoteDemo(MATNR, price, KONWA, LIFNR, IDNLF);
                    list.add(quoteDemo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    //查询库存
    public static List<StockDemo> getStock(String HHPN, String supplier, String supplierCode, String[] plantArr, Integer pweek) {
        List<StockDemo> list = new ArrayList<>();
        try {
            JCoDestination destination = JCoDestinationManager
                    .getDestination(ConnectPoolUtils.ABAP_AS_POOLED);
            destination.ping();
            String strFunc = STOCKFuction;
            JCoFunction funGetList = destination.getRepository().getFunction(
                    strFunc);
            JCoTable p_MAT_Table = funGetList.getTableParameterList().getTable(
                    "P_MAT");
            p_MAT_Table.deleteAllRows();

            p_MAT_Table.appendRow();
            p_MAT_Table.setRow(0);
            p_MAT_Table.setValue("MATNR", HHPN);

            JCoTable p_PLANT_Table = funGetList.getTableParameterList().getTable(
                    "P_PLANT");
            p_PLANT_Table.deleteAllRows();
            for (int i = 0; i < plantArr.length; i++) {
                p_PLANT_Table.appendRow();
                p_PLANT_Table.setRow(i);
                p_PLANT_Table.setValue("WERKS", plantArr[i]);
            }


            JCoParameterList JCoParameterList = funGetList.getImportParameterList();
            JCoParameterList.setValue("P_WEEK", pweek);

            funGetList.execute(destination);
            JCoParameterList struct = funGetList.getExportParameterList();
            String MARK = (String) struct.getValue("MARK");
//			String MESSAGE = (String) struct.getValue("MESSAGE");
            if ("N".equals(MARK.toUpperCase())) {
            	Utils.infoMessage("SAP接口连接失败!");			
                return null;
            }

            JCoTable return_Table = funGetList.getTableParameterList().getTable(
                    "RETURN");
            if (return_Table == null || return_Table.getNumRows() == 0) {
            	Utils.infoMessage("未查询到有效数据,请检查!");		
                return null;
            } else {
                for (int i = 0; i < return_Table.getNumRows(); i++) {
                	String matnr=(String)return_Table.getValue("MATNR"); //鸿海料号				
					String stock=(String)return_Table.getValue("STOCK");  //库存	
//					String stock = new DecimalFormat("0.00").format(STOCK);
					String totalst=(String)return_Table.getValue("TOTALST"); //总库存
					String planst=(String)return_Table.getValue("PLANST"); //生产计划数量
					String totalPlan=(String)return_Table.getValue("TOTALPLAN"); //总生产计划数量

                    StockDemo stockDemo = new StockDemo(matnr, supplier, supplierCode, stock, totalst, planst, totalPlan);
                    list.add(stockDemo);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    //检查冲突件 by汪亚洲
    public ArrayList<StockDemo> getCheckHHPN(String hhpn) {
        list = new ArrayList<>();
        try {
            JCoDestination destination = JCoDestinationManager
                    .getDestination(ConnectPoolUtils.ABAP_AS_POOLED);
            destination.ping();
            String strFunc = "ZRFC_GET_PROD_MASTER";
            JCoFunction funGetList = destination.getRepository().getFunction(
                    strFunc);
            JCoParameterList params = funGetList.getImportParameterList();
            params.setValue(3, hhpn);
            funGetList.execute(destination);
            JCoTable return_Table = funGetList.getTableParameterList().getTable(
                    "PROD_MASTER");
            System.out.println(return_Table);
            for (int i = 0; i < return_Table.getNumRows(); i++) {
                String baseUnit = (String) return_Table.getValue(5);
                String mt = (String) return_Table.getValue(2);

                System.out.println("hhpn ==== " + hhpn);
                System.out.println("baseUnit ==== " + mt);
                System.out.println("mt ==== " + mt);
                stockDemo = new StockDemo();
                stockDemo.setBaseunit(baseUnit);
                stockDemo.setMaterialType(mt);
                list.add(stockDemo);
            }
        } catch (Exception e) {
            System.out.print(e);
        }
        return list;
    }

}

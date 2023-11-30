package com.smallworld;


import com.smallworld.data.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Launcher {
    @SuppressWarnings("unchecked")
    public static void main(String[] args)
    {
        TransactionDataFetcher transactionDataFetcher = new TransactionDataFetcher();

        System.out.println("TOTAL TRANSACTION AMOUNT : "+ transactionDataFetcher.getTotalTransactionAmount());
        System.out.println("TOTAL TRANSACTION AMOUNT SENT BY : " +transactionDataFetcher.getTotalTransactionAmountSentBy("Tom Shelby"));
        System.out.println("MAX TRANSACTION AMOUNT : " +transactionDataFetcher.getMaxTransactionAmount());
        System.out.println("UNIQUE CLIENTS : "+ transactionDataFetcher.countUniqueClients());
        System.out.println("HAS OPEN ISSUES : "+transactionDataFetcher.hasOpenComplianceIssues("Grace Burgess"));
        System.out.println("TRANSACTIONS BY BENEFICIARY NAME : "+transactionDataFetcher.getTransactionsByBeneficiaryName());
        System.out.println("UNSOLVED IDS : "+transactionDataFetcher.getUnsolvedIssueIds());
        System.out.println("SOLVED ISSUE MESSAGES :" +transactionDataFetcher.getAllSolvedIssueMessages());
        System.out.println("TOP 3 TRANSACTIONS : "+transactionDataFetcher.getTop3TransactionsByAmount());
        System.out.println("TOP SENDER : "+transactionDataFetcher.getTopSender().get());

    }
}

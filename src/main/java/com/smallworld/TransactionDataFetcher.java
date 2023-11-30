package com.smallworld;

import com.smallworld.data.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.*;

public class TransactionDataFetcher {

    /**
     * Returns the sum of the amounts of all transactions
     */
    public double getTotalTransactionAmount() throws UnsupportedOperationException {

        //getting sum of all unique transactions, since repeated transactions are because of multiple issues
        //considering transaction with multiple issues as single transaction
        double amount = 0.0;
        for (List<Transaction> t : getMapOfTransactionsFromJSONFile().values()) {
            amount += t.get(0).getAmount();
        }
        return amount;
    }

    /**
     * Returns the sum of the amounts of all transactions sent by the specified client
     */
    public double getTotalTransactionAmountSentBy(String senderFullName) throws UnsupportedOperationException {
        double amount = 0.0;

        for(List<Transaction> t : getMapOfTransactionsFromJSONFile().values()){
            Transaction filteredTransaction = t.stream()
                    .filter(tr->tr.getSenderFullName().equals(senderFullName))
                    .findFirst().orElse(null);
            if(filteredTransaction != null)
                amount += filteredTransaction.getAmount();
            }
        return amount;
    }

    /**
     * Returns the highest transaction amount
     */
    public double getMaxTransactionAmount()  {

        return getListOfTransactionsFromJSONFile().stream()
                .mapToDouble(Transaction::getAmount)
                .max().orElseThrow(UnsupportedOperationException::new);
    }

    /**
     * Counts the number of unique clients that sent or received a transaction
     */
    public long countUniqueClients() throws UnsupportedOperationException {
        return getMapOfTransactionsFromJSONFile().size();
    }

    /**
     * Returns whether a client (sender or beneficiary) has at least one transaction with a compliance
     * issue that has not been solved
     */
    public boolean hasOpenComplianceIssues(String clientFullName) throws UnsupportedOperationException {
       Transaction t2 =  getListOfTransactionsFromJSONFile().stream()
                .filter(t->t.getSenderFullName().equals(clientFullName) || t.getBeneficiaryFullName().equals(clientFullName))
                .toList()
                .stream()
                .filter(t1-> !t1.getIssueSolved())
                .findFirst().orElse(null);
       return t2 != null;
    }

    /**
     * Returns all transactions indexed by beneficiary name
     */
    public Map<String, Transaction> getTransactionsByBeneficiaryName() throws UnsupportedOperationException {
        Map<String, Transaction> transactionMap = new HashMap<>();
        getListOfTransactionsFromJSONFile().forEach(transaction -> {
            transactionMap.put(transaction.getBeneficiaryFullName(),transaction);
        });
        return transactionMap;
    }

    /**
     * Returns the identifiers of all open compliance issues
     */
    public Set<Integer> getUnsolvedIssueIds() throws UnsupportedOperationException {
        Set<Integer> unsolvedIssueIds = new HashSet<>();
        List<Transaction> transactions = getListOfTransactionsFromJSONFile();
        for(Transaction t : transactions){
            if(!t.getIssueSolved())
                unsolvedIssueIds.add(t.getIssueId().intValue());
        }
        return unsolvedIssueIds;
    }

    /**
     * Returns a list of all solved issue messages
     */
    public List<String> getAllSolvedIssueMessages() throws UnsupportedOperationException {
        List<Transaction> transactions = getListOfTransactionsFromJSONFile();
        List<String> solvedIssueMessages = new ArrayList<>();
        for(Transaction t : transactions){
            if(t.getIssueSolved() && t.getIssueMessage() != null)
                solvedIssueMessages.add(t.getIssueMessage());
        }
        return solvedIssueMessages;
    }

    /**
     * Returns the 3 transactions with the highest amount sorted by amount descending
     */
    public List<Transaction> getTop3TransactionsByAmount() throws UnsupportedOperationException {
        List<Transaction> sortedList = new ArrayList<>();
        List<Transaction> top3TransactionsByAmount = new ArrayList<>();
        for (List<Transaction> t : getMapOfTransactionsFromJSONFile().values()) {
            sortedList.add(t.get(0));
        }
        sortedList.sort(Comparator.comparing(Transaction::getAmount, Comparator.reverseOrder()));
        for(int i = 0; i < 3; i++){
            top3TransactionsByAmount.add(sortedList.get(i));
        }
        return top3TransactionsByAmount;
    }

    /**
     * Returns the senderFullName of the sender with the most total sent amount
     */
    public Optional<String> getTopSender() throws UnsupportedOperationException {
        Map<String, Double> senderWithAmountMap = new HashMap<>();
        for(List<Transaction> t : getMapOfTransactionsFromJSONFile().values()){
            String senderName = t.get(0).getSenderFullName();
            Double amount = t.get(0).getAmount();
            if(senderWithAmountMap.containsKey(senderName)){
                senderWithAmountMap.put(senderName, senderWithAmountMap.get(senderName)+amount);
            }else{
                senderWithAmountMap.put(senderName,amount);
            }
        }
        return senderWithAmountMap.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
    }

    public List<Transaction> getListOfTransactionsFromJSONFile() {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("transactions.json")) {
            Object obj = jsonParser.parse(reader);
            JSONArray transactions = (JSONArray) obj;

            //Iterate over Transaction array
            List<Transaction> transactionList = new ArrayList<Transaction>();
            transactions.forEach(transaction -> {
                Transaction parsedTransaction = new Transaction();
                JSONObject jsonObject = (JSONObject) transaction;
                parsedTransaction.setMtn((Long) jsonObject.get("mtn"));
                parsedTransaction.setAmount((Double) jsonObject.get("amount"));
                parsedTransaction.setSenderFullName((String) jsonObject.get("senderFullName"));
                parsedTransaction.setSenderAge((Long) jsonObject.get("senderAge"));
                parsedTransaction.setBeneficiaryFullName((String) jsonObject.get("beneficiaryFullName"));
                parsedTransaction.setBeneficiaryAge((Long) jsonObject.get("beneficiaryAge"));
                parsedTransaction.setIssueId((Long) jsonObject.get("issueId"));
                parsedTransaction.setIssueSolved((Boolean) jsonObject.get("issueSolved"));
                parsedTransaction.setIssueMessage((String) jsonObject.get("issueMessage"));

                transactionList.add(parsedTransaction);
            });




            return transactionList;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    public Map<Long,List<Transaction>> getMapOfTransactionsFromJSONFile(){
        Map<Long, List<Transaction>> transactionMap = new HashMap<>();

        getListOfTransactionsFromJSONFile().forEach(transaction -> {
            List<Transaction> mtnTransactions = getListOfTransactionsFromJSONFile()
                    .stream()
                    .filter(t -> t.getMtn().equals(transaction.getMtn()))
                    .toList();
            transactionMap.put(transaction.getMtn(),mtnTransactions);
        });

        return transactionMap;
    }

}

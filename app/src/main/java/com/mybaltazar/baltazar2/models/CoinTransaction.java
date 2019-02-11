package com.mybaltazar.baltazar2.models;

public class CoinTransaction
{
    public enum TransactionType
    {
        AskQuestion,
        AnswerQuestion,
        AnswerBaltazar,
        Buy,
        InviteFriend,
        ProfileCompletion,
        Register,
    }

    public int amount;
    public String date;
    public TransactionType type;
    public String sourceId;
}

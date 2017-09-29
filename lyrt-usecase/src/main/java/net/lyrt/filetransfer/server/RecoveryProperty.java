package net.lyrt.filetransfer.server;

import net.lyrt.Compartment;

public class RecoveryProperty {
    public int numberOfChunks;
    public int offset; //Index of chunk that has been sent
    public Compartment compartment;
    public boolean isRecovered = false;
}

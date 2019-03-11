package com.findmiin.business.local.manager.visualizer;

/**
 * Created by JonIC on 2017-03-14.
 */
// Data class to explicitly indicate that these bytes are the FFT of audio data
public class FFTData
{
    public FFTData(byte[] bytes)
    {
        this.bytes = bytes;
    }

    public byte[] bytes;
}

package com.findmiin.business.local.manager.visualizer;

/**
 * Created by JonIC on 2017-03-14.
 */
// Data class to explicitly indicate that these bytes are raw audio data
public class AudioData
{
    public AudioData(byte[] bytes)
    {
        this.bytes = bytes;
    }

    public byte[] bytes;
}

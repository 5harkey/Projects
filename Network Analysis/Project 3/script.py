from collections import Counter
import math

def main():    
    inputFile = 'Unicauca-dataset-April-June-2019-Network-flows.txt'
    #Top 3 most common
    print('Finding top 3 most common source ip, port, destination ip, port...')
    src_ip_mc, src_port_mc, dst_ip_mc, dst_port_mc = mostCommon(inputFile)
            
    #Create clusters for each
    
    print('Creating clusters for each...')
    createClusters(src_ip_mc, inputFile, 10, 'Source IP')
    print('Source IP clusters created.')
    createClusters(src_port_mc, inputFile, 11, 'Source Port')
    print('Source Port clusters created.')
    createClusters(dst_ip_mc, inputFile, 12, 'Destination IP')
    print('Destination IP clusters created.')
    createClusters(dst_port_mc, inputFile, 13, 'Destination Port')
    print('Destination Port clusters created.')
    
    #Process Clusters
    print('Processing Clusters...')
    index = 10
    mapping = {
        'Source IP' : ['Source Port', 'Destination IP', 'Destination Port'],
        'Source Port' : ['Source IP', 'Destination IP', 'Destination Port'],
        'Destination IP' : ['Source IP', 'Source Port', 'Destination Port'],
        'Destination Port' : ['Source IP', 'Source Port', 'Destination IP']
    }
    names = ['Source IP Addresses', 'Source Ports', 'Destination IP Addresses', 'Destination Ports']
    j = 0 
    for values in [src_ip_mc, src_port_mc, dst_ip_mc, dst_port_mc]:
        print(f'Top 3 {names[j]} by flow count:')
        for x, freq in values:
            print(f'\t{x} | {freq}')
        print()
        j += 1
    for s in ['Source IP', 'Source Port', 'Destination IP', 'Destination Port']:
        x, y, z = mapping[s]
        for i in range(3):
            a, b, c, d, e, f, fixed = processCluster(f'{s} Cluster{i+1}.txt', index)
            print(f'{s} Cluster{i+1}.txt - {fixed}:\n\ta) {a} flows\n\tb) {b} packets\n\tc) {c} bytes\n\td) {x} relative uncertainty: {d:.5f}\n\te) {y} relative uncertainty: {e:.5f}\n\tf) {z} relative uncertainty: {f:.5f}\n')
        index += 1    

def createClusters(top3freq, inputFile, check, name):
    top3 = [x for x, _ in top3freq]
    cluster_files = []

    # Open files for writing clusters
    for i in range(3):
        filename = f'{name} Cluster{i+1}.txt'
        cluster_files.append(open(filename, 'w'))

    with open(inputFile, 'r') as f:
        for line in f:
            fields = line.split()
            x = fields[check]

            if x in top3:
                index = top3.index(x)
                cluster_files[index].write(line)

    # Close all cluster files
    for file in cluster_files:
        file.close()            
    
def mostCommon(inputFile):

    uniq_srcip = Counter()
    uniq_srcport = Counter()
    uniq_dstip = Counter()
    uniq_dstport = Counter()
    
    with open(inputFile, 'r') as f:
        for line in f:
            fields = line.split()
            
            #Extracting variables...
            src_ip = fields[10]
            src_port = fields[11]
            dst_ip = fields[12] 
            dst_port = fields[13]
            #Mapping frequency
            uniq_srcip[src_ip] += 1
            uniq_srcport[src_port] += 1
            uniq_dstip[dst_ip] += 1
            uniq_dstport[dst_port] += 1
            
    return uniq_srcip.most_common(3), uniq_srcport.most_common(3), uniq_dstip.most_common(3), uniq_dstport.most_common(3)    

def processCluster(inFile, fieldIndex):
    mapping = {
        10: (11, 12, 13),
        11: (10, 12, 13),
        12: (10, 11, 13),
        13: (10, 11, 12)
    }

    var1, var2, var3 = mapping[fieldIndex]
    flowCount, packetCount, byteCount = 0, 0, 0
    v1, v2, v3 = [], [], []
    with open(inFile, 'r') as f:
        for line in f:           
            fields = line.split()
            
            fixed = fields[fieldIndex]
            v1.append(fields[var1])
            v2.append(fields[var2])
            v3.append(fields[var3])
            packet = fields[15]
            byte = fields[16]
            
            flowCount += 1
            packetCount += int(packet)
            byteCount += int(byte)
    return flowCount, packetCount, byteCount, cal_entropy(v1), cal_entropy(v2), cal_entropy(v3), fixed
            
def cal_entropy(values):
    total = 0
    entropy = 0
    freq_dict = {}
    
    for value in values:
        value = value.rstrip()
        total += 1
        
        if value in freq_dict:
           freq_dict[value] += 1
        else:
           freq_dict[value] = 1
    
    for v in freq_dict:
        f = freq_dict[v]
        prob = f / total
        entropy += -1 * prob * math.log(prob, 2)
        
    max_entropy = math.log(total, 2)
    
    std_entropy = entropy / max_entropy 
    
    return std_entropy

if __name__ == '__main__':
    main()
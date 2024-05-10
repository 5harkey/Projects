import pandas as pd

# Step 1: Read data from the text file
with open("netflow.project4.data.txt", "r") as file:
    data = file.readlines()

# Step 2: Parse data and convert it into a DataFrame
data = [line.strip().split() for line in data]
headers = data[0]
rows = data[1:]
df = pd.DataFrame(rows, columns=headers)

# Step 3: Filter data for flows with Source IP Address as '192.168.0.107'
filtered_df = df[df['SrcIPaddress'] == '192.168.0.107']

# Step 4: Sample 100 random flows
random_flows = filtered_df.sample(n=100)

# Step 5: Save selected flows into a new CSV file and a text file
random_flows.to_csv("random_flows.csv", index=False)

with open("random_flows.txt", "w") as file:
    # Write header
    header = "Start              End                Sif   SrcIPaddress    SrcP  DIf   DstIPaddress    DstP  P   Fl  Pkts        Octets\n"
    file.write(header)
    
    # Write each row with proper spacing
    for index, row in random_flows.iterrows():
        formatted_row = "{:<18} {:<18} {:<5} {:<15} {:<5} {:<5} {:<15} {:<5} {:<3} {:<3} {:<11} {:<11}\n".format(
            row["Start"], row["End"], row["Sif"], row["SrcIPaddress"], row["SrcP"],
            row["DIf"], row["DstIPaddress"], row["DstP"], row["P"], row["Fl"],
            row["Pkts"], row["Octets"]
        )
        file.write(formatted_row)

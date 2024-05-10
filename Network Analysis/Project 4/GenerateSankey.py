import plotly.graph_objects as go
import pandas as pd

# Read data from CSV
netflow_data = pd.read_csv("random_flows.csv")

# Mapping dictionary for protocol numbers to names
protocol_mapping = {
    1: "ICMP",
    6: "TCP",
    17: "UDP"
}

# Replace protocol numbers with names
netflow_data['P'] = netflow_data['P'].map(protocol_mapping)

# Create Sankey diagram data
source_ips = netflow_data["SrcIPaddress"].unique()
dest_ips = netflow_data["DstIPaddress"].unique()
dest_ports = netflow_data["DstP"].unique()
protocols = netflow_data["P"].unique()

node_labels = list(source_ips) + list(dest_ips) + list(dest_ports) + list(protocols)

sources = [source_ips.tolist().index(ip) for ip in netflow_data["SrcIPaddress"]]
targets_dest = [len(source_ips) + dest_ips.tolist().index(ip) for ip in netflow_data["DstIPaddress"]]
targets_ports = [len(source_ips) + len(dest_ips) + dest_ports.tolist().index(port) for port in netflow_data["DstP"]]
targets_protocols = [len(source_ips) + len(dest_ips) + len(dest_ports) + protocols.tolist().index(protocol) for protocol in netflow_data["P"]]

values = netflow_data["Pkts"].tolist()

# Define node colors
num_source_ips = len(source_ips)
num_dest_ips = len(dest_ips)
num_dest_ports = len(dest_ports)
num_protocols = len(protocols)
node_colors = ['red'] * num_source_ips + ['blue'] * num_dest_ips + ['green'] * num_dest_ports + ['yellow'] * num_protocols

links = []
for source, target_dest, target_port, target_protocol, value in zip(sources, targets_dest, targets_ports, targets_protocols, values):
    links.append((source, target_dest, value))
    links.append((target_dest, target_port, value))
    links.append((target_port, target_protocol, value))

# Create Sankey diagram
fig = go.Figure(data=[go.Sankey(
    
    valueformat = '.0f',
    valuesuffix = ' Packet(s)',
    
    node=dict(
        pad=15,
        thickness=20,
        line=dict(color="black", width=0.5),
        label=node_labels,
        color=node_colors
    ),
    link=dict(
        source=[link[0] for link in links],
        target=[link[1] for link in links],
        value=[link[2] for link in links]
    ))])

# Update layout and display diagram
fig.update_layout(title_text="Sankey Diagram: Network Traffic Analysis (Source IP: 192.168.0.107)", font_size=10)
fig.show()

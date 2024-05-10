from Product import Device
from datetime import datetime

class DataAccess:

    def __init__ (self):
        self.filename = 'devices.txt'
    
    def load(self):
        devices = []
        try:
            with open(self.filename, 'r') as file:
                for line in file:
                    id, name, model, sn, price, mdate = line.strip().split('|')
                    device = Device(int(id), name, model, sn, float(price), datetime.strptime(mdate, '%m-%d-%Y'))
                    devices.append(device)
                devices = sorted(devices, key=lambda device: device.id)
        except Exception as e:
            print('Error reading file', e)
        return devices

    def delete(self, id):
        devices = self.load()
        for device in devices:
            if device.id == id:
                devices.remove(device)
                self.save(devices)
                
    def update(self, device):
        devices = self.load()
        for index, dev in enumerate(devices):
            if dev.id == device.id:
                devices[index] = device
        self.save(devices)

    
    def search(self, name):
        list = []
        devices = self.load()
        for device in devices:
            if name in device.name:
                list.append(device)
        return list
        
    def search_id(self, id):
        devices = self.load()
        for device in devices:
            if device.id == id:
                return device
    
    def add(self, device):
        try:
            devices = self.load()
            devices.append(device)
            with open(self.filename, 'a') as file:
                file.write(self.deviceToString(device))
        except Exception as e:
            print('Error adding to file', e)


#Helper Methods
    def save(self, devices):
        try:
            devices = sorted(devices, key=lambda device: device.id)
            with open(self.filename, 'w') as file:
                for device in devices:
                    file.write(self.deviceToString(device))
        except Exception as e:
            print('Error saving file', e)

    def deviceToString(self, device):
        return f'{device.id}|{device.name}|{device.model}|{device.serial_number}|{device.price}|{datetime.strftime(device.manufacture_date, "%m-%d-%Y")}\n'
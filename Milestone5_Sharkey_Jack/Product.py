class Device:
    def __init__(self, id, name, model, serial_number, price, manufacture_date):
        self.id = id
        self.name = name
        self.model = model
        self.serial_number = serial_number
        self.price = price
        self.manufacture_date = manufacture_date

    def __str__(self):
        return f'Device ID: {self.id}\n\tName: {self.name}\n\tModel: {self.model}\n'\
        + f'\tSerial Number: {self.serial_number}\n\tPrice: {self.price}\n\tManufacture Date: {self.manufacture_date}\n'
    